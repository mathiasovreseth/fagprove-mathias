

import React, { useContext, useEffect } from 'react';
import { IsAdminContext, superRequest } from './App';
import { FlexDiv, TextSmall } from './BasicStyles';

import styled from 'styled-components';
import { Button } from './Button';
import { Link } from 'react-router-dom';
import { EditExaminationPage } from './EditExemenationPage';




const Cell = styled.div`
margin-right: .5em;
width: 2em;
padding: .25em;
box-sizing: border-box;
`;


const ExaminationDiv = styled.div `
  background-color: yellow;
  :hover {
    cursor: pointer;
  }
`;






export function CalendarPage() {
  const [data, setData] = React.useState();
  const [editData, setEdit] = React.useState({
    isEditing: false,
    id: undefined,
  });
  const adminConsumer = useContext(IsAdminContext);
  const hasPemission = adminConsumer === 'ROLE_ADMIN' || adminConsumer === 'ROLE_MANAGER';
  // async function GetExaminations() {
  //   const fetch = await superRequest('http://localhost:8080/examination/list', {
  //   });
  //   const res = await fetch;
  //
  //   console.log(res);
  //
  // }
  async function getExaminators() {
    const fetch = await superRequest(' http://localhost:8080/examination/calendar', {
      "startDate": "2020-03-09T00:00:00Z",
      "endDate": "2020-03-30T00:00:00Z"
    });
    const res = await fetch;

    setData(res);

  }


  useEffect(() => {
    // GetExaminations();
    getExaminators();
  }, []);
  return (
    <div style={{width: '100%', margin: '0 auto'}}>
      {editData.isEditing ?
        <div style={{width: '100%' }}>
        <EditExaminationPage id={editData.id}/>
        </div> :

        <div>
          <FlexDiv style={{justifyContent: 'flex-end'}}>
            {hasPemission &&
            <Link to={'/calendar/create/examination'}>
              <Button >Ny eksemenasjon</Button>
            </Link>
            }

          </FlexDiv>
          <div style={{margin: '0 auto', width: '50%'}}>
            <FlexDiv style={{}}>
              <Cell>Uke</Cell>
              <Cell>Dag</Cell>
              <Cell>år</Cell>
              <FlexDiv style={{marginLeft: '1em'}}>
                {data && data[0].examinators.map(examinator => {
                  return (
                    <FlexDiv style={{}}>
                      <Cell style={{width: '8em'}}>{examinator.name}</Cell>
                    </FlexDiv>
                  );
                })}
              </FlexDiv>
            </FlexDiv>

            {data && data.map((c) => {
              return (
                <FlexDiv style={{borderBottom: '1px solid #333'}}>
                  <div>
                    <div key={c.day}>
                      <FlexDiv style={{
                        marginRight: '1em'
                      }}>
                        <Cell>
                          {c.week}
                        </Cell>
                        <Cell>
                          {c.day}
                        </Cell>
                        <Cell>
                          {c.year}
                        </Cell>
                      </FlexDiv>
                    </div>
                  </div> {c.examinators.map(ex => {
                  return (
                    <Cell style={{width: '8em'}}>
                      {ex.isBusy ?
                        <div style={{backgroundColor: 'red'}}>Opptatt</div>:
                        ex.examinations && ex.examinations.length > 0 ?
                          <ExaminationDiv onClick={()=> {
                            if(hasPemission) {
                              setEdit({
                                isEditing: true,
                                id: ex.examinations[0].id,
                              })
                            }}
                          }>
                              <TextSmall>{ex.examinations[0].candidate.name}</TextSmall>
                              <TextSmall>{ex.examinations[0].candidate.region}</TextSmall>
                            </ExaminationDiv>: <div></div>
                      }

                    </Cell>
                  );
                })}

                </FlexDiv>
              );
            })}
          </div>
        </div>
      }

    </div>
  )
}