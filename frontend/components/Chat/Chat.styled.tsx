import { colors } from '@/utils'
import styled from '@emotion/styled'
export const ChatWrapper = styled.div`
	display: grid;
	grid-template-columns:  minmax(265px, 410px) minmax(550px, 1fr);
	column-gap: 20px;
	margin-top: 20px;
	height: 80vh;
	
	@media (max-width: 880px) {
		grid-template-columns: 1fr;
	}

`

export const Item = styled.div`
	display: flex;
	align-items: center;
	column-gap: 20px;
	padding: 10px;
	cursor: pointer;
	transition: all 0.1s ease;
	&:hover{
		box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
		border-radius: 20px;
	}
	h4{
		font-size: 22px;
		line-height: 34px;
	}
	img{

		object-fit: cover;
		border-radius: 50%;
		max-width: 60px;
		max-height: 60px;
		min-width: 60px;
		min-height: 60px;
	}
	span{
		font-size: 18px;
		line-height: 26px;
		color: ${colors.ThirdGreyTextColor};
	}
		@media (max-width: 1024px) {
			h4{
				font-size: 18px;
				line-height: 24px;
			}
		img{

			object-fit: cover;
			border-radius: 50%;
			max-width: 45px;
			max-height: 45px;
			min-width: 45px;
			min-height: 45px;
		}
	span{
		font-size: 16px;
		line-height: 26px;
		color: ${colors.ThirdGreyTextColor};
	}		  
		}
`