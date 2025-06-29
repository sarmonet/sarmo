'use client'

import { useTranslation } from 'next-i18next'
import Image from "next/legacy/image"
import { useEffect, useState } from 'react'
import { Title } from '../Title/Title'
import { SearchedItem, SearchedList, SearchedWrapper } from './Searched.styled'
type RecentSearch = {
	category: { id: number; name: string; imageUrl: string }
	subCategory: { id: number; name: string } | null
}

export const Searched = () => {
	// const [recentListings, setRecentListings] = useState<ISliderItem[]>([]);
	const [recent, setRecent] = useState<RecentSearch[]>([])
	const { t } = useTranslation('common')
	useEffect(() => {
		const stored = localStorage.getItem('recentCategories')
		if (stored) {
			try {
				const parsed = JSON.parse(stored)
				if (Array.isArray(parsed)) {
					setRecent(parsed)
				}
			} catch {
				console.warn('Ошибка парсинга recentCategories')
			}
		}
	}, [])

	return (
		<SearchedWrapper>
			<Title>{t('heroTitles.searched')}</Title>
			<SearchedList>
				{recent.map(item => (
					<SearchedItem
						key={`${item.category.id}-${item.subCategory?.id || 'none'}`}
					>
						<Image
							src={item.category.imageUrl}
							alt='image cat'
							width={76}
							height={76}
							className='max-w-[76px] max-h-[76px] rounded-[50%] object-cover aspect-square '
						/>
						<div className='flex-col'>
							<h3>
								{item.category.name.length > 11
									? item.category.name.substring(0, 12) + '...'
									: item.category.name}
							</h3>
							<p>{item?.subCategory?.name}</p>
						</div>
					</SearchedItem>
				))}
			</SearchedList>
		</SearchedWrapper>
	)
}
