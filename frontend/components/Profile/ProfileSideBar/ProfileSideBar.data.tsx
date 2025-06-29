import { IProfileSide } from './ProfileSideBar.interface'

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const GetProfileSideData = (t: any): IProfileSide[] => [
	{
		title: t('profilePage.basic'),
	},
	{
		title: t('profilePage.account'),
	},
	{
		title: t('profilePage.notifications'),
	},
	{
		title: t('profilePage.payment'),
	},
	{
		title:  t('profilePage.investInfo'),
	},
	
	
]