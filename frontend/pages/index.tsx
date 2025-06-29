

import { Hero } from '@/components/Hero/Hero'
import { HomeCategories } from '@/components/HomeCategories/HomeCategories'
import type { GetStaticProps } from 'next'
import { useTranslation } from 'next-i18next'
import { serverSideTranslations } from 'next-i18next/serverSideTranslations'
import Head from 'next/head'
import { Container } from '../components/Container/Container'
import { MightLike } from '../components/MightLike/MightLike'
import { Popular } from '../components/Popular/Popular'
import { Questions } from '../components/Questions/Questions'
import { RecentAdditions } from '../components/RecentAdditions/RecentAdditions'
import { Searched } from '../components/Searched/Searched'
export default function Home() {
	const { t } = useTranslation('common')

	return (
		<>
			<Head>
				<title>{t('Sarmo')}</title>
				<meta name='description' content={t('Начни свой бизнес с нами!')} />					
			</Head>
			<Hero/>
			<Container>
				<HomeCategories/>
				<Searched/>
				<RecentAdditions/>
				<Popular/>
				<MightLike/>
				<Questions/>
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