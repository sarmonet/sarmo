import { colors } from '@/utils'
import styled from '@emotion/styled'



export const CatalogFilterWrapper = styled.div`
	display: flex;
	flex-direction: column;
	width: 90%;
	justify-content: center;
	color: ${colors.subtitleTextColor};
	font-size: 18px;
	border-top: 1px solid ${colors.borderColor};
	border-bottom: 1px solid ${colors.borderColor};
	padding: 20px 0;
	@media(max-width: 1440px) {
		width: 85%;
	}
	@media(max-width: 1024px) {
		min-width: 100%;
	}
	`
export const CatalogFilterButtons = styled.div`
	display: flex;
	flex-direction: column;
	
	width: 90%;
	row-gap: 20px;
	margin-top: 20px;
	button{
		width: 85%;
	}
		@media(max-width: 1280px) {
				width: 65%;	
		}
		@media(max-width: 1024px) {
			transform: translateY(10%);
			padding: 20px 0;
			margin: 0 auto;
		}
		
`
