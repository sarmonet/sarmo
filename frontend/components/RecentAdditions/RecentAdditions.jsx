'use client'

import { getRandomListings } from '@/services/getListings'
import { useTranslation } from 'next-i18next'
import { useEffect, useState } from 'react'
import { SliderBlock } from '../SliderBlock/SliderBlock'
import { Title } from '../Title/Title'
import { RecentAdditionsWrapper } from './RecentAdditions.styled'
export const RecentAdditions = () => {
	 const [listings, setListings] = useState([]);
  const [isMounted, setIsMounted] = useState(false);
  const { t } = useTranslation('common');
	// TODO: СДЕЛАТЬ listings С ПРИВЯЗКОЙ К ISliderItem
  useEffect(() => {
    setIsMounted(true);
    const fetchListings = async () => {
      try {
        const data = await getRandomListings({ count: 4 });
        setListings(data);
      } catch (error) {
        console.error('❌ Ошибка загрузки объявлений:', error);
      }
    };
    fetchListings();
  }, []);

  if (!isMounted) return null;
	return (
				<RecentAdditionsWrapper >
					<Title>{t('heroTitles.recantAddition')}</Title>
					<SliderBlock listings={listings} />
				</RecentAdditionsWrapper>
	)
}
