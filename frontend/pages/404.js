import Link from "next/link"


export default function Custom404() {
  return (
    <div
      style={{
        background: `linear-gradient(0deg, rgba(34,193,195,1) 0%, rgba(173,143,77,1) 100%)`,
        textAlign: "center",
        paddingTop: "250px",
        paddingBottom: "250px",
      }}
    >
      <h1>404 - Page Not Found</h1>
      <p>The page you are looking for does not exist.</p>
      <Link href="/" style={{ color: "#fcfcfc", textDecoration: "underline" }}>
        Go Home
      </Link>
    </div>
  );
}
