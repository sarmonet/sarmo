import { Container } from "@/components/Container/Container"
import type { GetStaticProps } from 'next'
import { useTranslation } from "next-i18next"
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from "next/head"
import { ProfilePage } from "../../components/ProfilePage/ProfilePage"
export default function Profile() {
	const { t } = useTranslation()
	return (
		<>
			<Head>
				<title>{t("Профиль")}</title>
				<meta name="description" content={t("profile page page-description")} />
			</Head>
			<Container>
				<ProfilePage/>
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
