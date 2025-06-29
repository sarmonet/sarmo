import Administration from '@/components/Administration/Administration'
import { Container } from '@/components/Container/Container'
import type { GetStaticProps } from 'next'
import { useTranslation } from 'next-i18next'
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from 'next/head'
export default function AdministrationPage() {
	const { t } = useTranslation()
	return (
		<>
			<Head>
				<title>{t('Administration')}</title>
				<meta
					name='description'
					content={t('Administration page page-description')}
				/>
			</Head>
			<Container>
				<Administration />
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
