export const generateCodeVerifier = () => {
	const buffer = new Uint8Array(32)
	window.crypto.getRandomValues(buffer)
	return btoa(String.fromCharCode.apply(null, [...buffer]))
		.replace(/\+/g, '-')
		.replace(/\//g, '_')
		.replace(/=+$/, '');
}

export const generateCodeChallenge = async codeVerifier => {
	const encoder = new TextEncoder()
	const data = encoder.encode(codeVerifier)
	const digest = await window.crypto.subtle.digest('SHA-256', data)
	const base64Digest = btoa(
		String.fromCharCode.apply(null, [...new Uint8Array(digest)])
	)
		.replace(/\+/g, '-')
		.replace(/\//g, '_')
		.replace(/=+$/, '')
	return base64Digest
}
