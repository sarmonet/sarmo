
export const formatDate = (isoString: string): string => {
  const date = new Date(isoString);
  return new Intl.DateTimeFormat("ru-RU", {
    day: "numeric",
    month: "long",
    year: "numeric",
  }).format(date);
};


const formattedDate = formatDate("2025-03-14T20:42:47.821832");
console.log(formattedDate); 
