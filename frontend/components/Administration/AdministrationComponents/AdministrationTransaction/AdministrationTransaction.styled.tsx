import { colors } from '@/utils'
import styled from '@emotion/styled'

interface ITransactionWrapper {
	gridColumns?: number;
}
export const TransactionWrapper = styled.div`
	font-size: 18px;
	font-weight: 400;
	line-height: 26px;
	margin: 0 -80px;
`
export const TransactionTop = styled.div<ITransactionWrapper>`
	display: grid;
	grid-template-columns: repeat(${({ gridColumns }) => gridColumns || 8}, 1fr);
	justify-content: space-between;
	column-gap: 12px;
	font-size: 17px;
	background-color: ${colors.btnMainColor};
	border-radius: 16px 16px 0 0;
	color: ${colors.mainWhiteTextColor};
	p{
		padding: 12px 18px 18px 18px;
		border-right: 1px solid ${colors.borderColor};
	}
	
`
export const TransactionBlocks = styled.div`
	display: flex;
	flex-direction: column;
	border: 1px solid ${colors.borderColor};
	border-radius: 16px;
`
export const TransactionBlock = styled.div<ITransactionWrapper>`
	display: grid;
	grid-template-columns: repeat(${({ gridColumns }) => gridColumns || 8}, 1fr);
	justify-content: space-between;
	&:not(:last-child){
		border-bottom: 1px solid ${colors.borderColor};
	}
	svg{
		margin: 0 auto;
		transform: translate(-50%);
	}
	p{
		position: relative;
		padding: 12px 18px;
		border-right: 1px solid ${colors.borderColor};
		cursor: pointer;
		color: ${colors.subtitleTextColor};
		&:last-child{
			border-right: none;
		}
		&:nth-of-type(1){
		display: flex;
		align-items: center;
		column-gap: 8px;
		
		button{
			opacity: 0;
			visible: hidden;
			
		}
		
			&:hover{
				button{
					position: absolute;
					right: 20%;
					opacity: 1;
					visible: visible;
				}	
			}
		}
	}
	
`