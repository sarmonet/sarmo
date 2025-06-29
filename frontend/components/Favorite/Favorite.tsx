import { delFavorite } from '@/services/getListings'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from "next/legacy/image"
import { useRouter } from 'next/router'
import { FaRegTrashAlt } from "react-icons/fa"
import { useCatalog } from '../Catalog/CatalogContext/CatalogContext'
import {
  Favorite,
  ListingAbout,
  ListingBlock,
  ListingMain,
  ListingSpecial,
  ListingTop,
  Premium,
} from '../Catalog/CatalogMain/CatalogMain.styled'
import { useDevice } from '../hooks/useDevice'
import { MoreBtn } from '../MoreBtn/MoreBtn'
import { ProfileMain, ProfileWrapper } from '../Profile/Profile.styled'
import { Title } from '../Title/Title'
import { FavoriteItems } from './Favorite.styled'
export const FavoriteTab = () => {
  const { favorite , updateFavorite , user } = useCatalog();
  const { isTablet, isMobile } = useDevice();
  const { t } = useTranslation('common');
  const router = useRouter();
  const listingPush = (id: number) => {
    router.push(`/listing/${id}`);
  };
const handleDeleteFavorite = async (listingId: number) => {
  try {
    await delFavorite(listingId);
    updateFavorite();
   
  } catch (error) {
    console.error("Ошибка удаления из избранного:", error);
  }
};
  return (
    <ProfileWrapper>
      <Title>{t('sideProfile.favorite')}</Title>
      <ProfileMain>
        <FavoriteItems>
          {favorite && favorite.length > 0 ? ( 
            favorite.map((item) => (
                <ListingBlock key={item.id}>
                  {user && (
                    favorite?.some((fav) => fav.id === item.id) && (
                      <Favorite>
                        <FaRegTrashAlt onClick={() => handleDeleteFavorite(item.id)} color="red" size={24} />
                      </Favorite>
                    ) 
                  )}
                
                  <div className="image-container" onClick={(isTablet || isMobile) ? () => listingPush(item.id) : undefined}>
                  <ListingSpecial color={colors.btnSecondColor}>{item.premiumSubscription === true && <Premium>топ</Premium>}</ListingSpecial>
                    <Image src={item.mainImage} alt={item.title} width={256} height={256} />
                  </div>
                  <ListingMain onClick={(isTablet || isMobile) ? () => listingPush(item.id) : undefined}>
                    <ListingTop>
                      <h3>{item.title}</h3>
                      <p>{item.category.name}</p>
                       {item.invest && <p style={{color:colors.btnSecondColor , marginLeft: '5px'}}>{t('buttons.investor')}</p> }
                    </ListingTop>
                    <ListingAbout>
                      <p>{item.city}, {item.country}</p>
                      <span>от {Number(item.price).toLocaleString('de-DE')} $ </span>
                    </ListingAbout>

                    <MoreBtn onClick={() => listingPush(item.id)}>{t('buttons.more')}</MoreBtn>

                  </ListingMain>
                </ListingBlock>
            ))
          ) : (
            <p>{t('profileFavorite.noContent')}</p>
          )}
        </FavoriteItems>
      </ProfileMain>
    </ProfileWrapper>
  );
};