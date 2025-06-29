import { Container } from '@/components/Container/Container'
import type { GetStaticProps } from 'next'
import { useTranslation } from 'next-i18next' // вот это
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from 'next/head'
import { Registration } from '../../components/Registration/Registration'
export default function RegistrationPage() {
	const { t } = useTranslation()
	return (
		<>
			<Head>
				<title>{t('login page page-title')}</title>
				<meta name='description' content={t('login page page-description')} />
			</Head>
			<Container>
				<Registration />
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
