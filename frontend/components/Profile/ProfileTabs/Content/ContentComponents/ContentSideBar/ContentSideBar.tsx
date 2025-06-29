import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import {
	ProfileSideBarItem,
	ProfileSideBarItems,
} from '../../../../ProfileSideBar/ProfileSideBar.styled'
import { GetContentSideData } from './ContentSideBar.data'

interface Props {
	setActiveTab: (index: number) => void
	activeTab: number
}
export const ContentSideBar = ({ setActiveTab, activeTab }: Props) => {
	const { t } = useTranslation('common')
	const ContentSideData = GetContentSideData(t)
	return (
		<ProfileSideBarItems>
			{ContentSideData.map((item, index) => {
				return (
					<ProfileSideBarItem
						key={index}
						onClick={() => setActiveTab(index)}
						style={
							activeTab === index
								? {
										backgroundColor: `${colors.profileBgColor}`,
										color: `${colors.mainTextColor}`,
								  }
								: {}
						}
					>
						{item.title}
					</ProfileSideBarItem>
				)
			})}
		</ProfileSideBarItems>
	)
}
