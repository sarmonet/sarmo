
export interface IUser {
	id: number;
	profilePictureUrl: string;
	name: string;
	email: string;
	phoneNumber: number;
	roles: {
		id: number,
		name: string,
	}[];
}
export interface ISubscriptionUser {
	id: number;
	firstName: string;
	lastName: string;
	registrationDate: string;
	userSubscription?: IUserSubscription;
	userIndividualFeature?: IUserIndividualFeature[];
}

export interface IPackagingDetails {
	status: string;
	presentationSelected: boolean;
	financialModelSelected: boolean;
	pageDesignSelected: boolean;
	listingId: number;
	userId: number;
}

export interface ITransactionSupport {
	userId: number;
	listingId: number;
	negotiationsAndAnalysis: boolean;
	preliminaryContractConclusion: boolean;
	businessDueDiligence: boolean;
	financialAnalysis: boolean;
	mainContractConclusion: boolean;
	postDealSupport: boolean;
}
export interface IUserSubscription {
	id: number;
	userId: number
	startDate: number;
	endDate?: number;
	subscriptionPlan: ISubscriptionPlan;
	status: "ACTIVE" | "INACTIVE" | "CANCELED";
}
export interface ISubscriptionPlan {
	id: number;
	name: string;
	price: number;
	billingCycle: "MONTHLY" | "YEARLY" | "QUARTERLY";
	description?: string;
	planFeatures?: IPlanFeature[];
}

export interface IPlanFeature {
	id: number;
	subscriptionPlan: ISubscriptionPlan;
	subscriptionFeature: ISubscriptionFeature;
	value: number | boolean;
	unit: string;
}

export interface ISubscriptionFeature {
	id: number;
	name: string;
	displayName: string;
	description: string;
	subscriptionFeatureType: 'BOOLEAN' | 'NUMBER' | 'TEXT';
}

export interface IIndividualFeature {
	id: number;
	name: string;
	displayName: string;
	description: string;
	individualFeatureType: 'BOOLEAN' | 'NUMBER' | 'TEXT';
	price: number;
}
export interface IUserIndividualFeature {
	id: number;
	user: ISubscriptionUser[];
	individualFeature: IIndividualFeature;
	purchaseDate: string;
	expirationDate?: string;
	userIndividualFeatureStatus: "ACTIVE" | "INACTIVE" | "EXPIRED" | "PENDING";
	additionalInfo?: string;
}

