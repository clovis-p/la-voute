#!/bin/sh

set -e

if [ "${BASIC_AUTH_ENABLED:-false}" = "true" ]; then
	if [ -z "${BASIC_AUTH_USERNAME}" ] || [ -z "${BASIC_AUTH_PASSWORD}" ]; then
		echo "BASIC_AUTH_ENABLED=true but BASIC_AUTH_USERNAME/BASIC_AUTH_PASSWORD is not set" >&2
		exit 1
	fi

	hash="$(caddy hash-password --plaintext "${BASIC_AUTH_PASSWORD}")"
	export CADDY_BASIC_AUTH="basic_auth {
		${BASIC_AUTH_USERNAME} ${hash}
	}"
else
	export CADDY_BASIC_AUTH=""
fi

exec caddy run --config /etc/caddy/Caddyfile --adapter caddyfile
