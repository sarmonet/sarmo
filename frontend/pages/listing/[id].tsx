import { Container } from '@/components/Container/Container'
import { Listing } from '@/components/Listing/Listing'
import type { GetStaticPaths, GetStaticProps } from 'next'
import { useTranslation } from 'next-i18next'
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from 'next/head'
export default function ListingPage() {
	const { t } = useTranslation()

	return (
		<>
			<Head>
				<title>{t('Listing')}</title>
				<meta name='description' content={t('profile page page-description')} />
			</Head>
			<Container>
				<Listing />
			</Container>
		</>
	)
}
export const getStaticPaths: GetStaticPaths = async ({ locales }) => {
	const paths =
		locales?.map(locale => ({
			params: { id: '' },
			locale,
		})) ?? []
	console.log('paths', paths)
	return {
		paths: [],
		fallback: 'blocking',
	}
}

export const getStaticProps: GetStaticProps = async ({ params, locale }) => {
	const id = params?.id as string

	return {
		props: {
			...(await serverSideTranslations(locale!, ['common'])),
			id,
		},
	}
}
