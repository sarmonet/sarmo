// import { getSubscriptionUsers } from '@/services/administration'
// import { useEffect, useState } from 'react'
// import { FaCheckCircle } from "react-icons/fa"
// import { IoChatbubbleOutline, IoCloseCircle } from "react-icons/io5"
// import { ISubscriptionUser } from '../AdministrationAcc/Admin.interface'
// import { TransactionBlock, TransactionBlocks, TransactionTop, TransactionWrapper } from '../AdministrationTransaction/AdministrationTransaction.styled'

export const AdministrationSubscribe = () => {
	// const [subscriptionUser, setSubscriptionUser] = useState<ISubscriptionUser>();
	// const { userSubscription } = subscriptionUser || {};
  // const subscriptionPlanName = userSubscription?.subscriptionPlan?.name;
  // const endDate = userSubscription?.endDate;

  // useEffect(() => {
  //   const fetchTransactionSupport = async () => {
  //     try {
  //       const response = await getSubscriptionUsers(); 
       
  //       setSubscriptionUser(response[0]); 
      
  //     } catch (error) {
  //       console.error("Error fetching transaction support:", error);
  //     }
  //   };

  //   fetchTransactionSupport();
  // }, []);

	return (
		<div>
			
		{/* {subscriptionUser.map((item) => (
			<div key={item.userId}>
				<p>{item.listingId}</p>
				<p>{item.negotiationsAndAnalysis && "sosi"}</p>
				<p>{item.preliminaryContractConclusion && "sosi"}</p>
				<p>{item.businessDueDiligence && "sosi"}</p>
				<p>{item.financialAnalysis && "sosi"}</p>
				<p>{item.mainContractConclusion && "sosi"}</p>
				<p>{item.postDealSupport && "sosi"}</p>
			</div>
		))} */}
		<div>
			{/* <TransactionWrapper>
				<TransactionBlocks>
					<TransactionTop gridColumns={5}>
						<p>ID Пользователя</p>
						<p>С</p>
						<p>До</p>
						<p>Standart</p>
						<p>Diamond</p>
					</TransactionTop>
				 <TransactionBlock gridColumns={5}>
              <p>{subscriptionUser?.id} <button><IoChatbubbleOutline size={24}/></button> </p>
              <p>{userSubscription?.startDate}</p>
              <p>{endDate ? <FaCheckCircle fill='green' size={23}/> : <IoCloseCircle fill='red' size={23}/>}</p>
              <p>{endDate ? <FaCheckCircle fill='green' size={23}/> : <IoCloseCircle fill='red' size={23}/>}</p>
							<p>
                {subscriptionPlanName === 'Standart' || subscriptionPlanName === 'Diamond' ? (
                  <FaCheckCircle fill='green' size={23}/>
                ) : (
                  <IoCloseCircle fill='red' size={23}/>
                )}
              </p>
            </TransactionBlock>
				</TransactionBlocks>
			</TransactionWrapper> */}
	</div>
		
		</div>
	);
}
