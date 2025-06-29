import Chat from '@/components/Chat/Chat'
import { Container } from '@/components/Container/Container'
import type { GetStaticProps } from 'next'
import { useTranslation } from 'next-i18next' // вот это
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from 'next/head'
export default function ChatPage() {
	const { t } = useTranslation()
	return (
		<>
			<Head>
				<title>{t('Чат Sarmo')}</title>
				<meta name='description' content={t('Чат')} />
			</Head>
			<Container>
				<Chat />
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
