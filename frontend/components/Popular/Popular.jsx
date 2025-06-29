'use client';

import { getRandomListings } from '@/services/getListings'
import { useTranslation } from 'next-i18next'
import { useEffect, useState } from 'react'
import { SliderBlock } from '../SliderBlock/SliderBlock'
import { Title } from '../Title/Title'
import { PopularWrapper } from './Popular.styled'
export const Popular = () => {
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
    <PopularWrapper>
      <Title>{t('heroTitles.popular')}</Title>
      <SliderBlock listings={listings} />
    </PopularWrapper>
  );
};
