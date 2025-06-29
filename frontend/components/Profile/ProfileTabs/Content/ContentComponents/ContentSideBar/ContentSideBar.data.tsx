import { IContentSide } from './ContentSideBar.interface'
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const GetContentSideData = (t: any): IContentSide[] => [
	{
		title: t('contentAside.blogTitle'),
	},
	{
		title: t('contentAside.newsTitle'),
	},
]
