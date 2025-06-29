import { BlogPage } from '@/components/Blog/BlogComponents/BlogPage'
import { Container } from '@/components/Container/Container'
import type { GetStaticPaths, GetStaticProps } from 'next'
import { useTranslation } from 'next-i18next'
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from 'next/head'
export default function ArticlePage() {
	const { t } = useTranslation()
	return (
		<>
			<Head>
				<title>{t('Blog page')}</title>
				<meta name='description' content={t('blog page page-description')} />
			</Head>
			<Container>
				<BlogPage />
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
