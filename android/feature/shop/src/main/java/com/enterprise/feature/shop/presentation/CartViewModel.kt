package com.enterprise.feature.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.core.domain.entities.CartItem
import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.usecases.AddToCartUseCase
import com.enterprise.feature.shop.domain.usecases.RemoveFromCartUseCase
import com.enterprise.feature.shop.domain.usecases.UpdateCartQuantityUseCase
import com.enterprise.core.domain.repositories.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

/** MVI ViewModel for the Shopping Cart feature. */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val addToCartUseCase: AddToCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase
) : ViewModel() {

    data class State(
        val items: List<CartItem> = emptyList(),
        val cartItemCount: Int = 0,
        val isLoading: Boolean = false,
        val error: DomainError? = null
    ) {
        val totalPrice: BigDecimal
            get() = items.fold(BigDecimal.ZERO) { acc, item -> acc + item.totalPrice }
    }

    sealed class Intent {
        data object LoadCart : Intent()
        data class AddToCart(val item: CartItem) : Intent()
        data class RemoveFromCart(val itemId: String) : Intent()
        data class UpdateQuantity(val itemId: String, val quantity: Int) : Intent()
        data object ClearCart : Intent()
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        processIntent(Intent.LoadCart)
    }

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadCart -> observeCart()
            is Intent.AddToCart -> addToCart(intent.item)
            is Intent.RemoveFromCart -> removeFromCart(intent.itemId)
            is Intent.UpdateQuantity -> updateQuantity(intent.itemId, intent.quantity)
            is Intent.ClearCart -> clearCart()
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                _state.update {
                    it.copy(
                        items = items,
                        cartItemCount = items.sumOf { item -> item.quantity },
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun addToCart(item: CartItem) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = addToCartUseCase(item)) {
                is DomainResult.Success -> _state.update { it.copy(isLoading = false) }
                is DomainResult.Failure -> _state.update {
                    it.copy(isLoading = false, error = result.error)
                }
            }
        }
    }

    private fun removeFromCart(itemId: String) {
        viewModelScope.launch {
            when (val result = removeFromCartUseCase(itemId)) {
                is DomainResult.Success -> { /* Room Flow auto-updates */ }
                is DomainResult.Failure -> _state.update { it.copy(error = result.error) }
            }
        }
    }

    private fun updateQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            when (val result = updateCartQuantityUseCase(itemId, quantity)) {
                is DomainResult.Success -> { /* Room Flow auto-updates */ }
                is DomainResult.Failure -> _state.update { it.copy(error = result.error) }
            }
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}
