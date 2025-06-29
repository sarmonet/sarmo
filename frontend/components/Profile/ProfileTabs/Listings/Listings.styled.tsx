import { colors } from '@/utils'
import styled from '@emotion/styled'


export const ListingWrapper = styled.div`
	display:flex;
	flex-direction:column;
	row-gap:40px;
	
`
export const ListingTop = styled.div`
	display:grid;
	grid-template-columns:repeat(2, 1fr);
	align-items:center;
	@media (max-width: 520px) {
	grid-template-columns:repeat(1, 1fr);
	}
`
export const ListingTitle = styled.div`
`
export const ListingInvest = styled.div`
	display: flex;
	align-items: center;
	text-align: center;
	justify-content: space-around;

	
`
export const ListingMain = styled.div`
	display:grid;
	grid-template-columns:repeat(2,1fr);
	flex-wrap:wrap;
	gap:20px;
	align-items:center;
	margin-bottom:70px;
	.checkbox{
		width: 20px;
		height: 20px;
	}
	.textarea{
		outline:none;
		padding:10px;
		box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.1);
		width:100%;
	}
		@media (max-width: 768px) {
			grid-template-columns:repeat(1, 100%);
		}
		
`
export const ListingFields = styled.div`
	display:grid;
	align-items:center;
	gap:30px;
	grid-template-columns:repeat(2,1fr);
	p{
		// min-width: 400px;
		font-size:18px;
	}
	@media (max-width: 768px) {
		grid-template-columns:repeat(1,1fr);
	}		
		
`
export const ListingField = styled.div`
	display:flex;
	flex-direction:column;
	row-gap:10px;
	input{
		padding:10px;
		border:1px solid ${colors.borderColor};
		border-radius:5px;
		outline:none;
	 white-space: nowrap; 
		overflow: hidden;    
		text-overflow: ellipsis; 
}
		.textarea{
			outline:none;
			padding:10px;
			box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.1);
			width:100%;

	}
`
export const ListingLabel = styled.label`
	font-weight:500;
	font-size:18px;
	line-height:26px;
	color:${colors.mainTextColor};
`
export const ListingButton = styled.button`
	font-weight:500;
	font-size:18px;
	line-height:26px;
	width: fit-content;
	padding:10px 20px;
	background-color:${colors.btnMainColor};
	border-radius:8px;
	color:${colors.mainWhiteTextColor};
`