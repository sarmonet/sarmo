import { Catalog } from '@/components/Catalog/Catalog'
import { Container } from "@/components/Container/Container"
import type { GetStaticProps } from 'next'
import { useTranslation } from 'next-i18next'
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from "next/head"

export default function CatalogPage() {
	const { t } = useTranslation()
	return (
		<>
			<Head>
				<title>{t("Каталог Sarmo")}</title>
				<meta name="description" content={t("Catalog page page-description")} />
			</Head>
				<Container>
					<Catalog/>
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


