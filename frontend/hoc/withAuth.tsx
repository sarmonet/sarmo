import { useRouter } from "next/router"
import { useEffect } from "react"

const withAuth = <P extends object>(WrappedComponent: React.ComponentType<P>) => {
  const WithAuth = (props: P) => {
    const router = useRouter();

    useEffect(() => {
      const checkAuth = async () => {
        const accessToken = localStorage.getItem("accessToken");
        if (!accessToken) {
          router.push("/login"); 
        }
      };
      checkAuth();
    }, [router]);

    return <WrappedComponent {...props} />;
  };

  WithAuth.displayName = `WithAuth(${WrappedComponent.displayName || WrappedComponent.name || 'Component'})`;
  
  return WithAuth;
};

export default withAuth;