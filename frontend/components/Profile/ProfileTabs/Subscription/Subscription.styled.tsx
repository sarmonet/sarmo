import { colors } from '@/utils'
import styled from '@emotion/styled'
interface isPremium {
	isPremium?: boolean
}
export const SubscriptionWrapper = styled.div`
	
`
export const SubscriptionItems = styled.div`
	display: grid;
	grid-template-columns: repeat(2, minmax(300px, 350px));
	gap: 35px;
	margin: 0 auto;
`
export const SubscriptionItem = styled.div<isPremium>`
	display: flex;
	flex-direction: column;
	gap: 10px;
	border-radius: 12px;
	position: relative;
	padding: 25px ;
	border: 1px solid ${colors.borderColor};
	box-shadow: 0px 4px 20px rgba(0, 0, 0, 0.1);
  height: 100%;
	
`
export const SubscriptionHead = styled.div`
	display: flex;
	margin-top: 30px;
`
export const SubscriptionRole = styled.div<isPremium>`
	position: absolute;
	top: 3%;
	right: 0;
	text-transform: uppercase;
	color: ${colors.mainWhiteTextColor};
	font-size: 21px;
	font-weight: 700;
	border-radius:180px 0 0 180px;
	padding: 20px 30px 20px 40px;

  background: ${(props) =>
    props.isPremium
      ? `linear-gradient(to right, green, red)`
      : `linear-gradient(to bottom, #4600D9, #7D3FFF)`};
`
export const SubscriptionBody = styled.div`
	display: flex;
	margin-top: 20px;
	flex-direction: column;
	gap: 40px;
`
export const SubscriptionPrice = styled.span`
	font-size: 31px;
	color: ${colors.btnMainColor};
  line-height: 24px;
	font-weight: 700;
`
export const SubscriptionDescription = styled.p`
	font-size: 16px;
	color: ${colors.categoryLink};
  line-height: 28px;
	max-width: 400px;
	padding-right: 20px;
	margin-top: 15px;
	text-align: center;
`
export const SubscriptionList = styled.ul`
	display: flex;
	flex-direction: column;
	flex-wrap: wrap;
	gap: 15px;
	height: 100%;
	overflow: hidden;
	color: ${colors.btnMainColor};
	li{
			display: flex;
			align-items: center;
			gap: 10px;
			font-size: 16px;
			line-height: 28px;
			font-weight: 700;
			svg{
				color: ${colors.btnSecondColor};
				min-width: 18px;
				max-width: 18px;
		}
`
export const SubscriptionButton= styled.button`
	font-size: 24px;
	color: ${colors.btnSecondColor};
	text-transform: uppercase;
	margin-top: auto;
	padding: 20px 30px;
	font-weight: 700;
`
