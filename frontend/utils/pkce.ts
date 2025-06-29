 
export async function generatePKCE() {
  const random = window.crypto.getRandomValues(new Uint8Array(32));
  const codeVerifier = btoa(String.fromCharCode(...random))
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
    .replace(/=+$/, "");

  const encoder = new TextEncoder();
  const data = encoder.encode(codeVerifier);
  const digest = await window.crypto.subtle.digest("SHA-256", data);

  const codeChallenge = btoa(String.fromCharCode(...new Uint8Array(digest)))
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
    .replace(/=+$/, "");

  localStorage.setItem("code_verifier", codeVerifier);
  return codeChallenge;
}



