import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { createChat } from '@/services/chat'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from 'next/legacy/image'
import { IoChatbubbleOutline } from 'react-icons/io5'
import { MdOutlinePhone } from 'react-icons/md'
import { ListingSpecialist, ListingSpecialistHat } from '../../Listing.styled'
import { ListingButton } from '../ListingButton'
interface IUser {
	id: number
	firstName: string
	lastName: string
	profileImageUrl: string
	email: string
	phoneNumber: number
}
export const ListingSpecialistBlock = (userInfo: IUser) => {
	const { user, setChatId } = useCatalog()
	const { t } = useTranslation('common')
	const handleChat = async (participantIds: number) => {
		try {
			await createChat(participantIds)
			setChatId(participantIds)
		} catch (error) {
			console.error('❌ Ошибка при получении чата:', error)
		}
	}
	return (
		<ListingSpecialist>
			<ListingSpecialistHat>
				<div
					style={{
						display: 'flex',
						alignItems: 'center',
						justifyContent: 'start',
						gap: '25px',
					}}
				>
					<Image
						src={userInfo.profileImageUrl || '/images/user/altUser.png'}
						alt='alternative'
						width={90}
						height={90}
					/>
					<div>
						<h3>
							{userInfo.firstName} {userInfo.lastName}
						</h3>
					</div>
				</div>
				{user && (
					<>
						{userInfo.phoneNumber && (
							<a href={`tel:${userInfo.phoneNumber}`}>
								<ListingButton
									title={t('buttons.call')}
									image={<MdOutlinePhone />}
									bgcolor={`transaprent`}
									color={`${colors.SecondGreyTextColor}`}
									border={`1px solid ${colors.borderColor}`}
								/>
							</a>
						)}
						<a href={`/chat?chatId=${userInfo.id}`}>
							<ListingButton
								title={t('buttons.write')}
								image={<IoChatbubbleOutline />}
								onClick={() => handleChat(userInfo.id)}
								bgcolor={`${colors.btnSecondColor}`}
								color={`${colors.mainWhiteTextColor}`}
								border={`1px solid ${colors.borderColor}`}
							/>
						</a>
					</>
				)}
			</ListingSpecialistHat>
		</ListingSpecialist>
	)
}
