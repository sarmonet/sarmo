import { ReactNode } from "react"

export interface ISideBar  {
	icon: ReactNode,
	title:string,
	link?: string,
	adminOnly?: boolean
}