import { FC, ReactNode } from "react"
import { MoreButton } from "./MoreBtn.style"

interface MoreBtnProps {
  children: ReactNode;
  onClick?: () => void; 
  color?: string;
  bgcolor?: string;
}

export const MoreBtn: FC<MoreBtnProps> = ({ children, onClick, color , bgcolor }) => {
  return <MoreButton color={color} bgcolor={bgcolor} onClick={onClick}>{children}</MoreButton>;
};
