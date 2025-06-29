'use client'

import { DropDown } from '@/components/DropDown/DropDown'
import ListingSearch from '@/components/ListingSearch/ListingSearch'
import { FilterParams, getListingsByFilter } from '@/services/getListings'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import { useState } from 'react'
import { useCatalog } from '../Catalog/CatalogContext/CatalogContext'
import { Container } from '../Container/Container'
import { Title } from '../Title/Title'
import { HeroContent, HeroHat, HeroWrapper, SearchBtn } from './Hero.styled'
export const Hero = () => {
	const router = useRouter()

	const { listings, setFilteredListings } = useCatalog()
	const [pendingFilters, setPendingFilters] = useState<Record<string, unknown>>(
		{}
	)
	const [loading, setLoading] = useState(false)
	const { t } = useTranslation('common')
	const handleDropDownChange = (key: string, value: unknown) => {
		if (value === undefined || value === null) return
		setPendingFilters(prev => ({
			...prev,
			[key]: value,
		}))
	}

	const handleApplyAndSend = (event: React.FormEvent) => {
		event.preventDefault()

		setTimeout(() => {
			router.push({
				pathname: '/catalog',
				query: {
					externalInit: 'true',
				},
			})
		}, 150)

		const filterParams: FilterParams = {
			sortBy: '',
			sortOrder: 'asc',
			sqlFilters: {
				category: pendingFilters.categoryType
					? parseInt(pendingFilters.categoryType as string, 10)
					: null,
				subCategory: null,
				city: null,
				country: (pendingFilters.country as string | null | undefined) || null,
				minPrice: pendingFilters.minPrice
					? parseInt(pendingFilters.minPrice as string, 10)
					: null,
				maxPrice: pendingFilters.maxPrice
					? parseInt(pendingFilters.maxPrice as string, 10)
					: null,
			},
			mongoFilters: {},
		}

		setLoading(true)

		getListingsByFilter(filterParams)
			.then(data => {
				if (data) {
					setFilteredListings({
						premiumListings: data.premiumListings || [],
						paginatedListings: data.paginatedListings || [],
					})
				}
			})
			.finally(() => {
				setLoading(false)
			})
	}

	return (
		<HeroWrapper>
			<Container>
				<HeroContent>
					<HeroHat>
						<Title isWhite={true}>{t('heroTitles.heroTitle')}</Title>
						<ListingSearch listings={listings} />
					</HeroHat>
					<form onSubmit={handleApplyAndSend}>
						<DropDown
							width='239px'
							isRounded={true}
							placeholder={t('placeholders.chooseCategory')}
							options={[
								{ value: '1', label: t('categories.franchise') },
								{ value: '2', label: t('categories.readyBusiness') },
								{ value: '3', label: t('categories.itStartup') },
								{ value: '4', label: t('categories.investments') },
								{ value: '5', label: t('categories.realEstate') },
								{ value: '6', label: t('categories.businessIdeas') },
								{ value: '7', label: t('categories.businessPlans') },
							]}
							onChange={value => handleDropDownChange('categoryType', value)}
						/>
						{/* <CustomSelect
            options={countryOptions}
            onChange={(selectedOption) =>
              handleDropDownChange('country', selectedOption?.label || '')
            }
            placeholder="Выберите страну"
            isSearchable
          /> */}

						<DropDown
							width='239px'
							isRounded={true}
							placeholder={t('placeholders.chooseCountry')}
							options={[
								{ value: 'Узбекистан', label: 'Узбекистан' },
								{ value: 'Казахстан', label: 'Казахстан' },
								{
									value: 'Киргизская Республика',
									label: 'Киргизская Республика',
								},
								{ value: 'Таджикистан', label: 'Таджикистан' },
								{ value: 'Туркменистан', label: 'Туркменистан' },
								{ value: 'Азербайджан', label: 'Азербайджан' },
								{ value: 'Грузия', label: 'Грузия' },
								{ value: 'Армения', label: 'Армения' },
								{ value: 'Украина', label: 'Украина' },
								{ value: 'Турция', label: 'Турция' },
								{ value: 'Россия', label: 'Россия' },
								{ value: 'Казахстан', label: 'Казахстан' },
								{ value: 'Республика Беларусь', label: 'Республика Беларусь' },
								{ value: 'Китай', label: 'Китай' },
							]}
							onChange={value => handleDropDownChange('country', value)}
						/>
						<DropDown
							width='239px'
							isRounded={true}
							placeholder={t('placeholders.priceFrom')}
							options={[
								{ value: '10000', label: '10.000 $' },
								{ value: '25000', label: '25.000 $' },
								{ value: '50000', label: '50.000 $' },
								{ value: '75000', label: '75.000 $' },
							]}
							onChange={value => handleDropDownChange('minPrice', value)}
						/>
						<DropDown
							width='239px'
							isRounded={true}
							placeholder={t('placeholders.priceTo')}
							options={[
								{ value: '500000', label: '500.000 $' },
								{ value: '1000000', label: '1.000.000 $' },
								{ value: '2000000', label: '2.000.000 $' },
								{ value: '3000000', label: '3.000.000 $' },
							]}
							onChange={value => handleDropDownChange('maxPrice', value)}
						/>
						<SearchBtn type='submit' disabled={loading}>
							{loading ? 'Поиск...' : <>{t('buttons.find')}</>}
						</SearchBtn>
					</form>
				</HeroContent>
			</Container>
		</HeroWrapper>
	)
}
