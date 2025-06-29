import { Container } from '@/components/Container/Container'
import { News } from '@/components/News/News'
import type { GetStaticProps } from 'next'
import { useTranslation } from 'next-i18next' // вот это
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from 'next/head'
export default function NewsPage() {
	const { t } = useTranslation()
	return (
		<>
			<Head>
				<title>{t('News')}</title>
				<meta name='description' content={t('news page page-description')} />
			</Head>
			<Container>
				<News />
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
