import { Blog } from '@/components/Blog/Blog'
import { Container } from '@/components/Container/Container'
import type { GetStaticProps } from 'next'
import { useTranslation } from 'next-i18next'
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from 'next/head'
export default function BlogPage() {
	const { t } = useTranslation()
	return (
		<>
			<Head>
				<title>{t('Blog')}</title>
				<meta name='description' content={t('blog page page-description')} />
			</Head>
			<Container>
				<Blog />
			</Container>
		</>
	)
}

export const getStaticProps: GetStaticProps = async ({ locale }) => {
	return {
		props: {
			...(await serverSideTranslations(locale!, ['common'])),
		},
	}
}
