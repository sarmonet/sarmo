export interface ICommentaries {
  id: number | null;
  content: string ;
  parentId: number | null;
  createdAt: string; 
  edited: boolean;
  author: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    profileImageUrl: string;
  }
}