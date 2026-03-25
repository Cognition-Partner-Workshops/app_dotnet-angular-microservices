# Enterprise Native Mobile Monorepo - Build Commands
.PHONY: build-all build-ios build-android test-all test-ios test-android lint-all lint-ios lint-android clean

# ─── Build ───────────────────────────────────────────────────────────────────

build-all: build-ios build-android

build-ios:
	@echo "==> Building iOS..."
	cd ios && swift build

build-android:
	@echo "==> Building Android..."
	cd android && ./gradlew assembleDebug

# ─── Test ────────────────────────────────────────────────────────────────────

test-all: test-ios test-android

test-ios:
	@echo "==> Testing iOS..."
	cd ios && swift test

test-android:
	@echo "==> Testing Android..."
	cd android && ./gradlew testDebugUnitTest

# ─── Lint ────────────────────────────────────────────────────────────────────

lint-all: lint-ios lint-android

lint-ios:
	@echo "==> Linting iOS..."
	cd ios && swiftlint lint --strict

lint-android:
	@echo "==> Linting Android..."
	cd android && ./gradlew ktlintCheck detekt

# ─── Clean ───────────────────────────────────────────────────────────────────

clean:
	@echo "==> Cleaning..."
	cd ios && swift package clean || true
	cd android && ./gradlew clean || true

# ─── CI ──────────────────────────────────────────────────────────────────────

ci: lint-all test-all build-all
	@echo "==> CI pipeline complete."
