import { colors } from '@/utils'
import styled from '@emotion/styled'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useEffect, useState } from 'react'
import { MdClose } from 'react-icons/md'
import { ICatalog } from '../Catalog/Catalog-data/CatalogInterface/Catalog.interface'
import { IListingItem } from '../Listing/Listing.interface'
interface ListingSearchProps {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  listings: ICatalog | any;
}

const StyledList = styled.ul`
  position: absolute;
  width: 100%;
  z-index: 10;
  background-color: #fcfcfc;
  border: 1px solid ${colors.borderColor};
  list-style: none;
  padding: 0;
  margin: 0;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

  li {
    padding: 10px 20px;
    cursor: pointer;
    transition: background-color 0.3s ease;

    &:hover {
      background-color: #f0f0f0;
    }
  }
`;

const CloseButton = styled.button`
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 20px;
  z-index: 20;
`;

const ListingSearch: React.FC<ListingSearchProps> = ({ listings }) => {
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [filteredListings, setFilteredListings] = useState<IListingItem[]>([]);
  const router = useRouter();
  const { t } = useTranslation('common');
  useEffect(() => {
    if (listings && searchQuery.length >= 1) {
      const filtered = Object.values(listings).filter(
        (listing) =>
          listing &&
          typeof listing === 'object' &&
          'title' in listing &&
          typeof listing.title === 'string' &&
          listing.title.toLowerCase().includes(searchQuery.toLowerCase())
      ) as IListingItem[];
      setFilteredListings(filtered);
    } else {
      setFilteredListings([]);
    }
  }, [searchQuery, listings]);

  const handleClose = () => {
    setSearchQuery('');
    setFilteredListings([]);
  };
const saveToRecentListings = (listing: IListingItem) => {
  try {
    const stored = localStorage.getItem('recentListings');
    let parsed: { data: IListingItem, timestamp: number }[] = stored
      ? JSON.parse(stored)
      : [];

    parsed = parsed.filter(item => item.data.id !== listing.id);

    parsed.unshift({ data: listing, timestamp: Date.now() });

    if (parsed.length > 10) parsed = parsed.slice(0, 10);

    localStorage.setItem('recentListings', JSON.stringify(parsed));
  } catch (e) {
    console.warn('Ошибка сохранения:', e);
  }
};


  const listingPush = (listingId: number) => {
    router.push({
      pathname: `/listing/${listingId}`,
    });
  };

  return (
    <div style={{ position: 'relative' }}>
      <input
        type="search"
        placeholder={t('placeholders.search')}
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        style={{ backgroundColor: '#fcfcfc'}}
      />
      {filteredListings.length > 0 && (
        <div style={{ position: 'relative' }}>
          <StyledList>
            {filteredListings.map((listing) => (
              <li
                key={listing.id}
                onClick={() => {
                  saveToRecentListings(listing); 
                  listingPush(listing.id);
                  setSearchQuery('');
                  setFilteredListings([]);
                }}
              >
                {listing.title}
              </li>
            ))}
          </StyledList>
          <CloseButton onClick={handleClose}>
            <MdClose />
          </CloseButton>
        </div>
      )}
    </div>
  );
};

export default ListingSearch;
