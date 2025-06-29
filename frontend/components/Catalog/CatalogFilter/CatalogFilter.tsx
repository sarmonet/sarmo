import { ListingButton } from '@/components/Listing/ListingComponents/ListingButton'
import { getCategoryMongoFilter } from '@/services/getListings'
import { postSubs } from '@/services/subscriptions'
import { colors } from '@/utils'
import { FC, useCallback, useEffect, useState } from 'react'
import { CiSearch } from 'react-icons/ci'
import { IoMdClose } from 'react-icons/io'
import { MdDataSaverOn } from 'react-icons/md'
import { useCatalog } from '../CatalogContext/CatalogContext'
import {
	CatalogFilterButtons,
	CatalogFilterWrapper,
} from './CatalogFilter.styled'
import { AdressFilter } from './CatalogFilterComponents/AdressFilter'
import { FilterWrapper } from './CatalogFilterComponents/components.styled'
import { NumberFilter } from './CatalogFilterComponents/NumberFilter'
import { RangeSlider } from './CatalogFilterComponents/RangeSlider'

interface IFilterProps {
	categoryId: number | null
	onFilterChange: React.Dispatch<React.SetStateAction<Record<string, unknown>>>
	applyFilters?: () => void
}

interface NumberFilterData {
	min: number
	max: number
}

export const CatalogFilter: FC<IFilterProps> = ({
	categoryId,
	onFilterChange,
	applyFilters,
}) => {
	const [resetKey, setResetKey] = useState<number>(0)
	const { setFilters, filteredParams } = useCatalog()
	const [serverFilters, setServerFilters] = useState<Record<
		string,
		unknown
	> | null>(null)
	const [pendingFilters, setPendingFilters] = useState<Record<string, unknown>>(
		{}
	)
	const [isFiltered, setIsFiltered] = useState(false)
	const [investFilter, setInvestFilter] = useState<boolean | null>(null)

	useEffect(() => {
		return () => {
			setFilters({})
			onFilterChange({})
			applyFilters?.()
		}
	}, [setFilters, onFilterChange, applyFilters])

	useEffect(() => {
		if (categoryId) {
			getCategoryMongoFilter({ id: categoryId }).then(data => {
				setServerFilters(data)
			})
		}
	}, [categoryId])

	const handleApplyAndSend = () => {
		const finalFilters = { ...pendingFilters }
		if (investFilter !== null) {
			finalFilters.invest = investFilter
		}
		setFilters(finalFilters)
		onFilterChange(finalFilters)
		setIsFiltered(true)
		applyFilters?.()
	}

	const handleResetAllFilters = () => {
		setPendingFilters({})
		setInvestFilter(null)
		setResetKey(prev => prev + 1)
		setFilters({})
		onFilterChange({})
		setIsFiltered(false)
		applyFilters?.()
	}

	const handleSubscribe = () => {
		setIsFiltered(false)
		const subscriptionPayload = {
			preferredCommunicationChannel: 'EMAIL',
			categorySubscription: {
				frequency: 'DAILY',
				filters: {
					filteredParams,
				},
				active: true,
			},
		}

		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		postSubs(subscriptionPayload as any)
			.then(() => {})
			.catch(error => {
				console.error('Ошибка при подписке:', error)
			})
	}

	const handleFilterChange = useCallback(
		(filterName: string, value: unknown, type?: string) => {
			// eslint-disable-next-line @typescript-eslint/no-explicit-any
			setPendingFilters((prevFilters: any) => {
				const newFilters = { ...prevFilters }

				if (['minPrice', 'maxPrice', 'country', 'city'].includes(filterName)) {
					newFilters[filterName] = value
				} else if (type === 'from') {
					newFilters[filterName] = { ...newFilters[filterName], min: value }
				} else if (type === 'to') {
					newFilters[filterName] = { ...newFilters[filterName], max: value }
				} else {
					newFilters[filterName] = value
				}

				return newFilters
			})
		},
		[]
	)

	const renderFilters = () => {
		if (!serverFilters) return null

		return Object.entries(serverFilters).map(([filterName, filterData]) => {
			if (filterName === 'sqlFilters') return null

			if (
				filterData &&
				typeof filterData === 'object' &&
				!Array.isArray(filterData)
			) {
				if ('min' in filterData && 'max' in filterData) {
					return (
						<NumberFilter
							key={`${filterName}-${resetKey}`}
							filterName={filterName}
							filterData={filterData as NumberFilterData}
							onFilterChange={handleFilterChange}
							// eslint-disable-next-line @typescript-eslint/no-explicit-any
							filters={pendingFilters as any}
							resetKey={resetKey}
						/>
					)
				}
			}
			return null
		})
	}

	return (
		<CatalogFilterWrapper>
			<RangeSlider
				key={`rangeSlider-${resetKey}`}
				onFilterChange={handleFilterChange}
			/>
			<AdressFilter
				key={`adressFilter-${resetKey}`}
				onFilterChange={handleFilterChange}
			/>

			<FilterWrapper>
				<label
					style={{
						display: 'flex',
						flexWrap: 'wrap',
						width: 'fit-content',
						alignItems: 'center',
						gap: '8px',
					}}
				>
					<span>Инвестиционное предложение:</span>
					<input
						type='checkbox'
						checked={investFilter === true}
						onChange={e => setInvestFilter(e.target.checked)}
					/>
				</label>
			</FilterWrapper>

			{serverFilters &&
			Object.keys(serverFilters).some(key => key !== 'sqlFilters') ? (
				<>
					<FilterWrapper>
						<span
							style={{
								fontWeight: '400',
								fontSize: '22px',
								display: 'flex',
								lineHeight: '34px',

								color: `${colors.btnMainColor}`,
								marginLeft: '10px',
							}}
						>
							Дополнительные фильтры
						</span>
					</FilterWrapper>

					{renderFilters()}

					<CatalogFilterButtons>
						<ListingButton
							onClick={handleApplyAndSend}
							title='Результаты'
							bgcolor={`${colors.btnSecondColor}`}
							color='#fcfcfc'
							image={<CiSearch />}
							border='none'
						/>
						<ListingButton
							onClick={handleResetAllFilters}
							title='Сбросить'
							border={`1px solid ${colors.borderColor}`}
							bgcolor='none'
							color='inherit'
							image={<IoMdClose />}
						/>
						{isFiltered && (
							<ListingButton
								onClick={handleSubscribe}
								title='Подписаться по текущим фильтрам'
								bgcolor={`${colors.btnMainColor}`}
								color='#ffffff'
								border='none'
								image={<MdDataSaverOn size={24} />}
							/>
						)}
					</CatalogFilterButtons>
				</>
			) : categoryId ? (
				<p>Дополнительные фильтры не найдены</p>
			) : null}
		</CatalogFilterWrapper>
	)
}
