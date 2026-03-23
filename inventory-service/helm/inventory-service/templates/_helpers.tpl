{{/*
---------------------------------------------------------------------------
Helm template helpers for the Inventory Service chart.

These named templates generate consistent names and labels used across
all Kubernetes manifests in the chart. Kubernetes requires resource names
to be at most 63 characters, so values are truncated accordingly.
---------------------------------------------------------------------------
*/}}

{{/*
Expand the name of the chart.
Uses .Values.nameOverride if provided; otherwise defaults to the chart name.
*/}}
{{- define "inventory-service.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a fully-qualified resource name by combining the Helm release name
with the chart name. Supports .Values.fullnameOverride for explicit control.
*/}}
{{- define "inventory-service.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}

{{/*
Common labels applied to every resource in the chart.
Includes the standard Kubernetes recommended labels for identification
and management.
*/}}
{{- define "inventory-service.labels" -}}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
app.kubernetes.io/name: {{ include "inventory-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels used by Deployments and Services to identify pods.
Must be a subset of the common labels to ensure consistent pod selection.
*/}}
{{- define "inventory-service.selectorLabels" -}}
app.kubernetes.io/name: {{ include "inventory-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
