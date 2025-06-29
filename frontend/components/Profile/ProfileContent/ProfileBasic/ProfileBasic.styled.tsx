import { colors } from '@/utils'
import styled from '@emotion/styled'
export const ProfileImagesBlock = styled.div`
	display: flex;
	align-items;center;
	flex-wrap: wrap;
	gap: 24px;
	img{
		max-width: 96px;
		max-height: 96px;
		border-radius: 50%;
	}
		@media (max-width: 991px) {
		display: flex;
		flex-wrap: wrap;
		gap: 12px;
			img{
				max-width: 86px;
				max-height: 86px;
			}
		}
`

export const ProfileUpload = styled.button`
	position: relative;
	padding: 12px 20px 12px 36px;
	font-weight: 700;
	font-size: 15px;
	line-height: 24px;
	height: fit-content;
	align-self: center;
	border-radius: 12px;
	background-color: ${colors.btnMainColor};
	color: ${colors.mainWhiteTextColor};
	transition: all 0.3s ease 0s;
	&:after{
		content: '+';
		position: absolute;
		left: 8px;
		top: 50%;
		transform: translateY(-55%); 
		font-weight: 300;
		font-size: 32px;
	}
	&:hover{
		background-color: ${colors.btnHoverColor};
	}

	@media (max-width: 991px) {
		font-size: 14px;
		padding: 10px 16px 10px 26px;
		&:after{
			font-size: 24px;
		}
	}
`
// export const ProfileRemove = styled.button`
// 	padding: 12px 20px ;
// 	font-weight: 700;
// 	font-size: 15px;
// 	line-height: 24px;
// 	height: fit-content;
// 	align-self: center;
// 	border-radius: 12px;
// 	background-color: transparent;
// 	border: 1px solid ${colors.SecondGreyTextColor};
// 	transition: all 0.3s ease 0s;

// 	&:hover{
// 		box-shadow: 0px 4px 4px 6px ${colors.profileBgColor}; 
// 	}
// 	@media (max-width: 991px) {
// 	font-size: 14px;
// 	padding: 12px 16px ;
// }
	
// `
export const ProfileDoc = styled.div`
	display: flex;
	flex-direction: column;
	gap: 12px;
	input{
		width: 100%;
		border-radius: 8px;
		color: ${colors.categoryLink};
	}	
}
	
`
export const ProfileDocBtn = styled.button`
	padding: 12px 20px ;
	font-weight: 700;
	font-size: 15px;
	line-height: 24px;
	height: fit-content;
	border-radius: 12px;
	width: fit-content;
	background-color: transparent;
	transition: all 0.3s ease 0s;
	color: ${colors.mainWhiteTextColor};
	background-color: ${colors.btnSecondColor};
	&:hover{
		box-shadow: 0px 4px 4px 6px ${colors.profileBgColor}; 
	}
	
`