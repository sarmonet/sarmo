
export const renderFieldContent = (value: unknown, type: string) => {
  switch (type) {
    case "string":
      return <p>{String(value)}</p>;

    case "list":
      return (
        <ul>
          {Array.isArray(value)
            ? value.map((item, index) => <li key={index}>{String(item)}</li>)
            : <li>{String(value)}</li>}
        </ul>
      );

    case "map":
      return (
        <div>
          {typeof value === "object" && value !== null
            ? Object.entries(value).map(([key, val]) => (
                <div key={key}>
                  <strong>{key}:</strong> {renderFieldContent(val, "string")}
                </div>
              ))
            : <p>{String(value)}</p>}
        </div>
      );

    default:
      return <p>{String(value)}</p>;
  }
};
