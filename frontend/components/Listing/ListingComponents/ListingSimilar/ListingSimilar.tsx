import {
  CatalogItems,
  CatalogItemsTitle,
  CatalogSimilar,
  ListingAbout,
  ListingBlock,
  ListingTop
} from '@/components/Catalog/CatalogMain/CatalogMain.styled'
import { MoreBtn } from '@/components/MoreBtn/MoreBtn'
import { Favorite } from '@/components/SliderBlock/SliderBlock.style'
import { useTranslation } from 'next-i18next'
import Image from "next/legacy/image"
import { useRouter } from 'next/router'
import { MdFavoriteBorder } from 'react-icons/md'
import { IListingItem } from '../../Listing.interface'
interface IListingItemProps {
  similarListings: IListingItem[]
}

export const ListingSimilar = ({ similarListings }:IListingItemProps) => {	
  const router = useRouter();
  const { t } = useTranslation('common');
  const listingPush = (id: number) => {
    router.push(`/listing/${id}`);
  };

  if (!similarListings || similarListings.length === 0) return null; 

  return (
    <>
      <CatalogItemsTitle className="listing-similar__title">
        Похожие объявления
      </CatalogItemsTitle>
      <CatalogSimilar className="listing-similar__list">
        {similarListings.map((listing) => (
          <ListingBlock key={listing.id}>
            <Favorite>
              <MdFavoriteBorder color="red" size={24} />
            </Favorite>
            <Image 
              src={listing.mainImage} 
              alt={listing.title} 
              width={254} 
              height={168} 
            />
            <CatalogItems>
              <ListingTop>
                <h3>{listing.title}</h3>
                <p>{listing.category.name}</p>
              </ListingTop>
              <ListingAbout>
                <p>
                  {listing.city}, {listing.country}
                </p>
                <span>от {listing.price} $</span>
              </ListingAbout>
              <MoreBtn onClick={() => listingPush(listing.id)}>
                {t('buttons.more')}
              </MoreBtn>
            </CatalogItems>
          </ListingBlock>
        ))}
      </CatalogSimilar>
    </>
  );
};
