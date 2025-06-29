import {
	AdminTab,
	AdminTabs,
} from '@/components/Administration/Administration.styled'
import { useTranslation } from 'next-i18next'
interface Props {
	setActiveTab: (index: number) => void
	activeTab: number
}

export const AdminArticlesTabs = ({ setActiveTab, activeTab }: Props) => {
	const { t } = useTranslation('common')
	const tabsData = [
		{
			title: t('menu.blog'),
		},
		{
			title: t('menu.news'),
		},
	]
	return (
		<AdminTabs>
			{tabsData.map((item, index) => (
				<AdminTab
					key={index}
					onClick={() => setActiveTab(index)}
					activeTab={activeTab}
					index={index}
				>
					{item.title}
				</AdminTab>
			))}
		</AdminTabs>
	)
}
