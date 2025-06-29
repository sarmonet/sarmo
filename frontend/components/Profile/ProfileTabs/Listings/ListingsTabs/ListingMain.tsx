import { ProfileContentWrapper } from '@/components/Profile/ProfileContent/ProfileContent.styled'
import { ListingCreate } from '../ListingComponents/LIstingContent'
import { ListingMyListings } from '../ListingComponents/ListingMyListings'

interface Props {
	activeTab: number
}

export const ListingMain = ({ activeTab }: Props) => {
	return(
		<ProfileContentWrapper>
			{activeTab === 0 &&  <ListingMyListings />}
			{activeTab === 1 &&  <ListingCreate />}
		</ProfileContentWrapper>
	)
}