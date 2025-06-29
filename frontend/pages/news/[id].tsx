import { Container } from '@/components/Container/Container'
import { NewsContent } from '@/components/News/NewsComponents/NewsPage'
import type { GetStaticPaths, GetStaticProps } from 'next'
import { useTranslation } from 'next-i18next'
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from 'next/head'

export default function NewsPage() {
	const { t } = useTranslation()
	return (
		<>
			<Head>
				<title>{t('News page')}</title>
				<meta name='description' content={t('news page page-description')} />
			</Head>
			<Container>
				<NewsContent />
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
