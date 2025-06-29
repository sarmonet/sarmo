import { ISubscriptionPlan } from '@/components/Administration/AdministrationComponents/AdministrationAcc/Admin.interface'
import { Title } from '@/components/Title/Title'
import { getSubs } from '@/services/subscriptions'
import { useTranslation } from 'next-i18next'
import { useEffect, useState } from 'react'
import { TfiCheckBox } from 'react-icons/tfi'
import { ProfileMain, ProfileWrapper } from '../../Profile.styled'
import {
	SubscriptionBody,
	SubscriptionButton,
	SubscriptionDescription,
	SubscriptionHead,
	SubscriptionItem,
	SubscriptionItems,
	SubscriptionList,
	SubscriptionPrice,
	SubscriptionRole,
} from './Subscription.styled'
export const SubscriptionTab = () => {
	const [subscriptions, setSubscriptions] = useState<ISubscriptionPlan[]>([])
	const { t } = useTranslation('common')
	useEffect(() => {
		const fetchSubscriptions = async () => {
			try {
				const response = await getSubs()
				setSubscriptions(response)
			} catch (error) {
				console.error('Error fetching subscriptions:', error)
			}
		}

		fetchSubscriptions()
	}, [])
	return (
		<ProfileWrapper>
			<Title>{t('sideProfile.subscriptions')}</Title>
			<ProfileMain>
				<SubscriptionItems>
					{subscriptions.map((item, index) => (
						<SubscriptionItem key={index}>
							<SubscriptionHead>
								<SubscriptionRole isPremium={item.name === 'PREMIUM'}>
									{item.name || 'name'}
								</SubscriptionRole>
								<SubscriptionPrice>
									{item.price || 'price'}$/mo
								</SubscriptionPrice>
							</SubscriptionHead>
							<SubscriptionBody>
								<SubscriptionDescription>
									{item.description}
								</SubscriptionDescription>
								{item.planFeatures && item.planFeatures.length > 0 && (
									<SubscriptionList>
										{item.planFeatures.map((feature, featureIndex) => (
											<li key={featureIndex}>
												<TfiCheckBox />
												{feature.subscriptionFeature.displayName}:{' '}
												{feature.value} {feature.unit || ''}
											</li>
										))}
									</SubscriptionList>
								)}
							</SubscriptionBody>

							<SubscriptionButton>get started</SubscriptionButton>
						</SubscriptionItem>
					))}
				</SubscriptionItems>
			</ProfileMain>
		</ProfileWrapper>
	)
}
