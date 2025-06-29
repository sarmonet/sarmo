
export const filterListings = (listings, filters) => {
  return listings.filter((listing) => {
    for (const key in filters) {
      if (filters.hasOwnProperty(key)) {
        const filterValue = filters[key];
        const listingValue = listing.fields[key];

        if (typeof filterValue === 'object' && filterValue !== null) {
          if (filterValue.min !== undefined && listingValue < filterValue.min) {
            return false;
          }
          if (filterValue.max !== undefined && listingValue > filterValue.max) {
            return false;
          }
        } else if (Array.isArray(filterValue)) {
          if (!filterValue.includes(listingValue)) {
            return false;
          }
        } else {
          if (listingValue !== filterValue) {
            return false;
          }
        }
      }
    }
    return true;
  });
};
