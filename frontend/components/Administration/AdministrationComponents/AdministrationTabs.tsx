import { useTranslation } from 'next-i18next'
import { AdminTab, AdminTabs } from '../Administration.styled'
interface Props {
	setActiveTab: (index: number) => void
	activeTab: number
}

export const AdministrationTabs = ({ setActiveTab, activeTab }: Props) => {
	const { t } = useTranslation('common')
	const tabsData = [
		{
			title: t('tabs.inactiveListings'),
		},
		{
			title: t('tabs.allListings'),
		},
		{
			title: t('tabs.analytics'),
		},
		{
			title: t('tabs.subscriptions'),
		},
		{
			title: t('tabs.accounts'),
		},
		{
			title: t('tabs.dealSupport'),
		},
		{
			title: t('tabs.projectPackaging'),
		},
		{
			title: t('tabs.createListing'),
		},
		{
			title: t('sideProfile.blog'),
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
