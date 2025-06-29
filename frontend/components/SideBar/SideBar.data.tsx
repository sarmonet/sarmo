import { BsCardChecklist } from 'react-icons/bs'
import { CgProfile } from 'react-icons/cg'
import { MdFavoriteBorder, MdOutlineAdminPanelSettings } from 'react-icons/md'
// import { PiContactlessPayment } from "react-icons/pi"
import { ISideBar } from './SideBar.interface'

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const GetSideBar = (t: any): ISideBar[] => [
	{ icon: <CgProfile />, title: t('sideProfile.profile') },
	{ icon: <MdFavoriteBorder />, title: t('sideProfile.favorite') },
	{ icon: <BsCardChecklist />, title: t('sideProfile.listings') },
	// {icon: <PiContactlessPayment size={20}/>, title: t('sideProfile.subscriptions')},
	{ icon: <CgProfile />, title: t('sideProfile.blog'), adminOnly: true },
	{
		icon: <MdOutlineAdminPanelSettings />,
		title: t('sideProfile.administration'),
		link: '/administration',
		adminOnly: true,
	},
]
