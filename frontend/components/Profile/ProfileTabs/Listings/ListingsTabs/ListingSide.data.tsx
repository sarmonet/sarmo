
export interface ListingSideData {
	title: string,
}
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const GetListingSideData= (t: any): ListingSideData[] => [
	{
		title: t('profileListingsAside.listingsMine'),
	},
	{
		title: t('profileListingsAside.listingsCreate'),
	},
	
]