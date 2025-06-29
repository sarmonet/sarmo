import { Container } from '@/components/Container/Container'
import type { GetStaticProps } from 'next'
import { useTranslation } from 'next-i18next'
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from 'next/head'
import { Login } from '../../components/Login/Login'

export default function LoginPage() {
	const { t } = useTranslation()
	return (
		<>
			<Head>
				<title>{t('login page page-title')}</title>
				<meta name='description' content={t('login page page-description')} />
			</Head>
			<Container>
				<Login />
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
