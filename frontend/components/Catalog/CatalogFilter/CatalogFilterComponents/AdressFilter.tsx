import { useTranslation } from 'next-i18next'
import { useEffect, useState } from 'react'
import { IoIosArrowUp } from 'react-icons/io'
import {
	Dropdown,
	FilterItem,
	FilterWrapper,
	SelectWrapper,
} from './components.styled'

interface AdressFilterProps {
	onFilterChange: (filterName: string, value: unknown) => void
}

export const AdressFilter = ({ onFilterChange }: AdressFilterProps) => {
	const [selectedCountry, setSelectedCountry] = useState<string | null>(null)
	const [selectedCity, setSelectedCity] = useState<string | null>(null)
	const [isCountryOpen, setIsCountryOpen] = useState<boolean>(false)
	const [isCityOpen, setIsCityOpen] = useState<boolean>(false)
	const [cities, setCities] = useState<string[]>([])
	const { t } = useTranslation('common')
	useEffect(() => {
		onFilterChange('country', selectedCountry)
		onFilterChange('city', selectedCity)
	}, [selectedCountry, selectedCity, onFilterChange])
	const countries = [
		{
			name: t('options.countries.ukraine'),
			code: 'UA',
			cities: [
				t('options.cities.kyiv'),
				t('options.cities.kharkiv'),
				t('options.cities.odesa'),
				t('options.cities.lviv'),
				t('options.cities.dnipro'),
			],
		},
		{
			name: t('options.countries.uzbekistan'),
			code: 'UZ',
			cities: [
				t('options.cities.tashkent'),
				t('options.cities.samarkand'),
				t('options.cities.bukhara'),
				t('options.cities.fergana'),
				t('options.cities.andijan'),
			],
		},
		{
			name: t('options.countries.kazakhstan'),
			code: 'KZ',
			cities: [
				t('options.cities.astana'),
				t('options.cities.almaty'),
				t('options.cities.shymkent'),
				t('options.cities.karaganda'),
				t('options.cities.aktobe'),
			],
		},
		{
			name: t('options.countries.kyrgyzRepublic'),
			code: 'KG',
			cities: [
				t('options.cities.bishkek'),
				t('options.cities.osh'),
				t('options.cities.jalalabad'),
				t('options.cities.karakol'),
				t('options.cities.tokmok'),
			],
		},
		{
			name: t('options.countries.tajikistan'),
			code: 'TJ',
			cities: [
				t('options.cities.dushanbe'),
				t('options.cities.khujand'),
				t('options.cities.kulob'),
				t('options.cities.qurghonteppa'),
				t('options.cities.istaravshan'),
			],
		},
		{
			name: t('options.countries.turkmenistan'),
			code: 'TM',
			cities: [
				t('options.cities.ashgabat'),
				t('options.cities.turkmenabat'),
				t('options.cities.dashoguz'),
				t('options.cities.mary'),
				t('options.cities.balkanabat'),
			],
		},
		{
			name: t('options.countries.azerbaijan'),
			code: 'AZ',
			cities: [
				t('options.cities.baku'),
				t('options.cities.ganja'),
				t('options.cities.sumgait'),
				t('options.cities.lankaran'),
				t('options.cities.mingachevir'),
			],
		},
		{
			name: t('options.countries.georgia'),
			code: 'GE',
			cities: [
				t('options.cities.tbilisi'),
				t('options.cities.batumi'),
				t('options.cities.kutaisi'),
				t('options.cities.rustavi'),
				t('options.cities.gori'),
			],
		},
		{
			name: t('options.countries.armenia'),
			code: 'AM',
			cities: [
				t('options.cities.yerevan'),
				t('options.cities.gyumri'),
				t('options.cities.vanadzor'),
				t('options.cities.echmiadzin'),
				t('options.cities.hrazdan'),
			],
		},
		{
			name: t('options.countries.turkey'),
			code: 'TR',
			cities: [
				t('options.cities.istanbul'),
				t('options.cities.ankara'),
				t('options.cities.izmir'),
				t('options.cities.bursa'),
				t('options.cities.antalya'),
			],
		},
		{
			name: t('options.countries.russia'),
			code: 'RU',
			cities: [
				t('options.cities.moscow'),
				t('options.cities.saintpetersburg'),
				t('options.cities.novosibirsk'),
				t('options.cities.yekaterinburg'),
				t('options.cities.kazan'),
			],
		},
		{
			name: t('options.countries.belarus'),
			code: 'BY',
			cities: [
				t('options.cities.minsk'),
				t('options.cities.gomel'),
				t('options.cities.mogilev'),
				t('options.cities.vitebsk'),
				t('options.cities.grodno'),
			],
		},
		{
			name: t('options.countries.china'),
			code: 'CN',
			cities: [
				t('options.cities.beijing'),
				t('options.cities.shanghai'),
				t('options.cities.chongqing'),
				t('options.cities.tianjin'),
				t('options.cities.guangzhou'),
			],
		},
	]
	const handleCountryClick = (country: { name: string; cities: string[] }) => {
		setSelectedCountry(country.name)
		setCities(country.cities)
		setSelectedCity(null)
		setIsCountryOpen(false)
	}

	const handleCityClick = (city: string) => {
		setSelectedCity(city)
		setIsCityOpen(false)
	}

	return (
		<FilterWrapper>
			<SelectWrapper>
				<FilterItem onClick={() => setIsCountryOpen(!isCountryOpen)}>
					{selectedCountry || 'Выберите страну'}
					<IoIosArrowUp />
				</FilterItem>
				{isCountryOpen && (
					<Dropdown>
						{countries.map(country => (
							<div
								key={country.code}
								onClick={() => handleCountryClick(country)}
							>
								{country.name}
							</div>
						))}
					</Dropdown>
				)}
			</SelectWrapper>

			<SelectWrapper>
				<FilterItem
					onClick={() => selectedCountry && setIsCityOpen(!isCityOpen)}
				>
					{selectedCity || 'Выберите город'}
					<IoIosArrowUp />
				</FilterItem>
				{isCityOpen && selectedCountry && (
					<Dropdown>
						{cities.map(city => (
							<div key={city} onClick={() => handleCityClick(city)}>
								{city}
							</div>
						))}
					</Dropdown>
				)}
			</SelectWrapper>
		</FilterWrapper>
	)
}
