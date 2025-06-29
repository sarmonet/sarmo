import { ProfileAccount } from './ProfileAccount/ProfileAccount'
import { ProfileBasic } from './ProfileBasic/ProfileBasic'
import { ProfileContentWrapper } from './ProfileContent.styled'
import { ProfileInvest } from './ProfileInvest/ProfileInvest'
import { ProfileNotification } from './ProfileNotification/ProfileNotification'
import { ProfilePayment } from './ProfilePayment/ProfilePayment'
interface Props {
	activeTab: number
}

export const ProfileContent = ({ activeTab }: Props) => {
	
	return(
		<ProfileContentWrapper>
			{activeTab === 0 &&  <ProfileBasic />}
			{activeTab === 1 &&   <ProfileAccount />}
			{activeTab === 2 &&   <ProfileNotification />}
			{activeTab === 3 &&  <ProfilePayment />}
			{activeTab === 4 &&  <ProfileInvest />}
		</ProfileContentWrapper>
	)


}