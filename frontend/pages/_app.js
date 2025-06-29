import { Provider } from '@/components/ui/provider'
import { LoadScript } from '@react-google-maps/api'
import { GoogleOAuthProvider } from '@react-oauth/google'
import { SessionProvider } from 'next-auth/react'
import { appWithTranslation } from 'next-i18next'
import dynamic from 'next/dynamic'
import { Lato } from 'next/font/google'
import Head from 'next/head'
import { useRouter } from 'next/router'
import React, { Suspense, useEffect, useState } from 'react'
import { CatalogProvider } from '../components/Catalog/CatalogContext/CatalogContext'
import { Loader } from '../components/Loader/Loader'
import nextI18NextConfig from '../next-i18next.config.js'
import '../styles/globals.css'

const lato = Lato({ subsets: ['latin'], weight: ['400', '700'] })

const Layout = dynamic(() => import('@/components/Layout/Layout'), {
	ssr: false,
	loading: () => <Loader />,
})

function MyApp({ Component, pageProps }) {
	const router = useRouter()
	const { locale } = router
	const [loading, setLoading] = useState(false)

	useEffect(() => {
		const handleRouteChange = () => setLoading(true)
		const handleRouteComplete = () => setLoading(false)

		router.events.on('routeChangeStart', handleRouteChange)
		router.events.on('routeChangeComplete', handleRouteComplete)
		router.events.on('routeChangeError', handleRouteComplete)

		return () => {
			router.events.off('routeChangeStart', handleRouteChange)
			router.events.off('routeChangeComplete', handleRouteComplete)
			router.events.off('routeChangeError', handleRouteComplete)
		}
	}, [router])

	return (
		<div className={lato.className}>
			<Head>
				<title>Sarmo</title>
				<meta name='description' content='Начни свой бизнес с нами!' />
			</Head>
			<LoadScript
				googleMapsApiKey={process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY}
			>
				<GoogleOAuthProvider
					clientId={process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID}
				>
					<SessionProvider session={pageProps.session}>
						<CatalogProvider>
							<Provider>
								<Suspense fallback={<Loader />}>
									<Layout>
										{loading ? (
											<Loader />
										) : (
											<Component {...pageProps} locale={locale} />
										)}
									</Layout>
								</Suspense>
							</Provider>
						</CatalogProvider>
					</SessionProvider>
				</GoogleOAuthProvider>
			</LoadScript>
		</div>
	)
}

export default appWithTranslation(MyApp, nextI18NextConfig)
