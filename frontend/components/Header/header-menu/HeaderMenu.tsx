import {
	ICatalog,
	ICatalogSub,
} from '@/components/Catalog/Catalog-data/CatalogInterface/Catalog.interface'
import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
// import { DropDown } from '@/components/DropDown/DropDown'
import { useDevice } from '@/components/hooks/useDevice'
import { getSubCategories } from '@/services/getCategories'
import { useCheckAdmin } from '@/utils/useCheckAdmin'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import { FC, useState } from 'react'
// import { TbWorld } from 'react-icons/tb'
import { List, Navigation } from './HeaderMenu.style'
import MenuItem from './menu-item/MenuItem'
import { getMenu } from './menu.data'

const HeaderMenu: FC = () => {
	const [isOpen, setIsOpen] = useState(false)
	const [activeCategory, setActiveCategoryMenu] = useState<number | null>(null)
	const {
		categories,
		setActiveCategory,
		setSelectedSubCategory,
		setCurrentPage,
	} = useCatalog()
	const [subCategories, setSubCategories] = useState<ICatalogSub[]>([])
	const isAdmin = useCheckAdmin()
	const { isDesktop } = useDevice()
	const router = useRouter()
	const { t } = useTranslation('common')
	if (!t || typeof t !== 'function') return null
	const menu = getMenu(t)
	if (isAdmin === null) return null
	const handleHover = (hover: boolean) => {
		setIsOpen(hover)
		if (!hover) setActiveCategoryMenu(null)
	}

	const handleCategoryHover = async (category: ICatalog) => {
		setActiveCategoryMenu(category.id)

		try {
			const data = await getSubCategories({ id: category.id })
			setSubCategories(data || [])
		} catch (error) {
			console.error('Ошибка загрузки подкатегорий:', error)
			setSubCategories([])
		}
	}

	const handleCategoryClick = (category: ICatalog) => {
		setActiveCategory(category)
		setSelectedSubCategory(null)
		handleHover(false)
		router.push({
			pathname: '/catalog',
		})
		setIsOpen(false)
	}

	const handleSubCategoryClick = (subCategory: ICatalogSub) => {
		const category = categories.find(cat => cat.id === activeCategory)
		if (category) {
			setActiveCategory(category)
			setSelectedSubCategory(subCategory)
			router.push({
				pathname: '/catalog',
				query: { categoryId: category.id, subCategoryId: subCategory.id },
			})
			setIsOpen(false)
		}
	}

	return (
		<Navigation>
			{isDesktop && (
				<>
					<List>
						{menu
							.filter(item => !item.adminOnly || isAdmin)
							.map(item => (
								<li
									key={item.link}
									onClick={
										item.name === 'Каталог'
											? () => setCurrentPage(1)
											: undefined
									}
									onMouseEnter={
										item.name === 'Каталог'
											? () => handleHover(true)
											: undefined
									}
									onMouseLeave={
										item.name === 'Каталог'
											? () => handleHover(false)
											: undefined
									}
								>
									<MenuItem item={item} />

									{item.name === 'Каталог' && isOpen && (
										<div className='dropdown'>
											<ul className='categories'>
												{categories.map(category => (
													<li
														key={category.id}
														onMouseEnter={() => handleCategoryHover(category)}
														onClick={() => handleCategoryClick(category)}
													>
														<a>{category.name}</a>
													</li>
												))}
											</ul>

											{activeCategory !== null && subCategories.length > 0 && (
												<div className='submenu'>
													{[0, 1, 2].map(colIndex => (
														<div key={colIndex} className='submenu-column'>
															{subCategories
																.filter(
																	(_, i) =>
																		Math.floor(
																			i / Math.ceil(subCategories.length / 3)
																		) === colIndex
																)
																.map((sub, i) => (
																	<span
																		key={i}
																		onClick={() => handleSubCategoryClick(sub)}
																	>
																		{sub.name}
																	</span>
																))}
														</div>
													))}
												</div>
											)}
										</div>
									)}
								</li>
							))}
					</List>
					{/* <DropDown
						value=<TbWorld size={24} className='relative right-[-60%]' />
						bgc='transparent'
						width='160px'
						options={[
							{ value: 'ru', label: 'RU' },
							{ value: 'uz', label: 'UZ ' },
							{ value: 'en', label: 'EN' },
						]}
						onChange={value => {
							router.push(router.pathname, router.asPath, { locale: value })
						}}
					/> */}
				</>
			)}
		</Navigation>
	)
}

export default HeaderMenu
