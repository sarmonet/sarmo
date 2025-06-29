'use client'
import { ICatalog } from '@/components/Catalog/Catalog-data/CatalogInterface/Catalog.interface'
import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { useCheckAdmin } from '@/utils/useCheckAdmin'
import { useTranslation } from 'next-i18next'
import Link from 'next/link'
import { useRouter } from 'next/router'
import { Container } from '../Container/Container'
import { HeaderLogo } from '../Header/header-logo/headerLogo'
import {
	GetFooterContact,
	GetFooterSections,
} from './Footer-navigations/Footer-data'
import {
	FooterAbout,
	FooterBody,
	FooterContent,
	FooterWrapper,
} from './Footer.styled'
const Footer = () => {
	const { setActiveCategory, setSelectedSubCategory, categories } = useCatalog()
	const router = useRouter()
	const isAdmin = useCheckAdmin()
	const { t } = useTranslation('common')
	if (!t || typeof t !== 'function') return null
	const FooterContact = GetFooterContact(t)
	const FooterSections = GetFooterSections(t)
	if (isAdmin === null) return null

	const handleCategoryClick = (category: ICatalog) => {
		setActiveCategory(category)
		setSelectedSubCategory(null)
		router.push({
			pathname: '/catalog',
		})
	}
	return (
		<FooterWrapper>
			<Container>
				<FooterContent>
					<div className='mt-[30px]'>
						<HeaderLogo isWhite={true} />
					</div>
					<FooterBody>
						<FooterAbout>
							<span>{t('footer.sections')}</span>
							{FooterSections.filter(item => !item.adminOnly || isAdmin).map(
								item => {
									return (
										<ul key={item.title}>
											<li>
												<Link href={item?.link ?? '#'}>{item.title}</Link>
											</li>
										</ul>
									)
								}
							)}
						</FooterAbout>
						<FooterAbout>
							<span>{t('footer.goods')}</span>
							<ul className='categories'>
								{categories.map(category => (
									<li
										key={category.id}
										onClick={() => handleCategoryClick(category)}
									>
										<a>{category.name}</a>
									</li>
								))}
							</ul>
						</FooterAbout>
						<FooterAbout>
							<span>{t('footer.contactsTitle')}</span>
							{FooterContact.map(item => {
								return (
									<ul key={item.title}>
										<li>
											<Link href={item?.link ?? '#'}>{item.title}</Link>
										</li>
									</ul>
								)
							})}
						</FooterAbout>
					</FooterBody>
				</FooterContent>
			</Container>
		</FooterWrapper>
	)
}

export { Footer }
